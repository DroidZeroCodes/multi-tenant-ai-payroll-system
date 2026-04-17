import {
    Breadcrumb,
    BreadcrumbItem,
    BreadcrumbLink,
    BreadcrumbList,
    BreadcrumbSeparator,
} from "@/components/ui/breadcrumb"
import {Link} from "@tanstack/react-router";

export interface BreadCrumbProps {
    path: Map<string, string> | null
}

export default function BreadCrumb({path}: BreadCrumbProps) {
    return (
        <Breadcrumb className="px-6 py-2.5 font-medium border-b-sm">
            <BreadcrumbList>
                <BreadcrumbItem>
                    <BreadcrumbLink asChild>
                        <Link to={"/"}>Home</Link>
                    </BreadcrumbLink>
                </BreadcrumbItem>
                {path && path.size > 0 && Array.from(path.entries()).map(([key, value]) => (
                    <>
                        <BreadcrumbSeparator/>
                        <BreadcrumbItem>
                            <BreadcrumbLink asChild>
                                <Link to={key}>{value}</Link>
                            </BreadcrumbLink>
                        </BreadcrumbItem>
                    </>
                ))}
            </BreadcrumbList>
        </Breadcrumb>
    )
}